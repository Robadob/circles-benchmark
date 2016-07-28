/*
 * Copyright 2011 University of Sheffield.
 * Author: Dr Paul Richmond 
 * Contact: p.richmond@sheffield.ac.uk (http://www.paulrichmond.staff.shef.ac.uk)
 *
 * University of Sheffield retain all intellectual property and 
 * proprietary rights in and to this software and related documentation. 
 * Any use, reproduction, disclosure, or distribution of this software 
 * and related documentation without an express license agreement from
 * University of Sheffield is strictly prohibited.
 *
 * For terms of licence agreement please attached licence or view licence 
 * on www.flamegpu.com website.
 * 
 */

#ifndef _FUNCTIONS_H_
#define _FUNCTIONS_H_

#include "header.h"

#define INTERACTION_RADIUS __RADIUS__f
#define INTERACTION_RADIUS2 __RADIUS2__f
#define ATTRACTION_FORCE __ATTRACT__f
#define REPULSION_FORCE __REPEL__f
#define WIDTH __WIDTH__f

__FLAME_GPU_FUNC__ int inputdata(xmachine_memory_Circle* xmemory, xmachine_message_location_list* location_messages, xmachine_message_location_PBM* partition_matrix)
{

  float x1, y1, z1, x2, y2, z2, fx, fy, fz, toLocX, toLocY, toLocZ;
  float separation;
  float k;
  x1 = xmemory->x;
  fx = 0.0;
  y1 = xmemory->y;
  fy = 0.0;
  z1 = xmemory->z;
  fz = 0.0;
    
  // Loop through all messages 
  xmachine_message_location* location_message = get_first_location_message(location_messages, partition_matrix, (float)xmemory->x, (float)xmemory->y, (float)xmemory->z);
  while(location_message)
  {
      if((location_message->id != xmemory->id))
      {
          x2 = location_message->x;
          y2 = location_message->y;
          z2 = location_message->z;
          
          toLocX = x2-x1;
          toLocY = y2-y1;
          toLocZ = z2-z1;
          if(toLocX!=0.0f||toLocY!=0.0f||toLocZ!=0.0f)
          {
            // Deep (expensive) check 
            separation = sqrt(toLocX*toLocX + toLocY*toLocY + toLocZ*toLocZ);
            if(separation < INTERACTION_RADIUS2)
            {
              k = (separation < INTERACTION_RADIUS) ? REPULSION_FORCE : ATTRACTION_FORCE;
              toLocX = (separation < INTERACTION_RADIUS) ? -toLocX : toLocX;
              toLocY = (separation < INTERACTION_RADIUS) ? -toLocY : toLocY;
              toLocZ = (separation < INTERACTION_RADIUS) ? -toLocZ : toLocZ;
              toLocX /= separation;//Normalize (without recalculating separation)
              toLocY /= separation;//Normalize (without recalculating separation)
              toLocZ /= separation;//Normalize (without recalculating separation)
              separation = (separation < INTERACTION_RADIUS) ? separation : (INTERACTION_RADIUS2 - separation);
              fx += k * separation * toLocX;
              fy += k * separation * toLocY;
              fz += k * separation * toLocZ;
            }
          }
      }
      //Move onto next message to check 
      location_message = get_next_location_message(location_message, location_messages, partition_matrix);
  }
  xmemory->fx = fx;
  xmemory->fy = fy;
  xmemory->fz = fz;
  
  return 0;
}

__FLAME_GPU_FUNC__ int outputdata(xmachine_memory_Circle* xmemory, xmachine_message_location_list* location_messages)
{
    float x, y, z;

  x = xmemory->x;
    y = xmemory->y;
  z = xmemory->z;
    
  add_location_message(location_messages, xmemory->id, x, y, z);

  return 0;
}

__FLAME_GPU_FUNC__ int move(xmachine_memory_Circle* xmemory)
{
    float x, y, z;
    //Move and Clamp
    x = xmemory->x + xmemory->fx;
    x = (x<0)?0:x;
    x = (x>WIDTH-1)?WIDTH-1:x;
    y = xmemory->y + xmemory->fy;
    y = (y<0)?0:y;
    y = (y>WIDTH-1)?WIDTH-1:y;
    z = xmemory->z + xmemory->fz;
    z = (z<0)?0:z;
    z = (z>WIDTH-1)?WIDTH-1:z;
  
  xmemory->x = x;
  xmemory->y = y;
  xmemory->z = z;
  return 0;
}


#endif // #ifndef _FUNCTIONS_H_
