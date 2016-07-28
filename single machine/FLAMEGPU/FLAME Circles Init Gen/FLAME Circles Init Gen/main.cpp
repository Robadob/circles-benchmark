#define _CRT_SECURE_NO_WARNINGS

#include <cstdio>
#include <string>
#include <algorithm>
#include <random>
#include <chrono>
#include <fstream>

#include "rapidxml.hpp"
#include "rapidxml_print.hpp"

int main(int argc, char**argv)
{
	if (argc<6)
	{
		printf("<executable> -w WIDTH -c AGENT_COUNT -f OUTPUT_FILE\n");
		exit(1);
	}
	unsigned int width = 0;
	unsigned int count = 0;
	std::string outFile = std::string("");
	std::string outFile2 = std::string("");
	for (int i = 0; i < argc; i++)
	{
		std::string arg(argv[i]);
		std::transform(arg.begin(), arg.end(), arg.begin(), ::tolower);
		if (arg.compare("-w") == 0)
		{
			width = (unsigned int)strtoul(argv[++i], nullptr, 0);
		}
		else if (arg.compare("-c") == 0)
		{
			count = (unsigned int)strtoul(argv[++i], nullptr, 0);
		}else if (arg.compare("-f") == 0)
		{
			outFile = std::string(argv[++i]);
		}
		else if (arg.compare("-f2") == 0)
		{
			outFile2 = std::string(argv[++i]);
		}
	}
	if (width == 0)
	{
		printf("Err: Width must be >0\n");
		exit(1);
	}
	if (count == 0)
	{
		printf("Err: Agent count must be >0\n");
		exit(1);
	}
	if (outFile.empty())
	{
		printf("Err: Output file name must be specified\n");
		exit(1);
	}
	//Actually do the generation
	rapidxml::xml_document<char> doc;
	//Common strings
	char *states_node_str = doc.allocate_string("states");
		char *itno_node_str = doc.allocate_string("itno");
		char *xagent_node_str = doc.allocate_string("xagent");
			char *name_node_str = doc.allocate_string("name");
			char *id_node_str = doc.allocate_string("id");
			char *x_node_str = doc.allocate_string("x");
			char *y_node_str = doc.allocate_string("y");
			char *z_node_str = doc.allocate_string("z");
			char *fx_node_str = doc.allocate_string("fx");
			char *fy_node_str = doc.allocate_string("fy");
			char *fz_node_str = doc.allocate_string("fz");
	char *circle_str = doc.allocate_string("Circle");
	char *zero_pt_zero_str = doc.allocate_string("0.0");
	//temp stuff
	char buffer[1024];
	float fBuffer[3];
	std::mt19937 rng((unsigned int)std::chrono::system_clock::now().time_since_epoch().count());  // mt19937 is a standard mersenne_twister_engine
	std::uniform_real_distribution<float> rng_float(0.0, width-1);
	//states
	rapidxml::xml_node<> *states_node = doc.allocate_node(rapidxml::node_element, states_node_str);
	doc.append_node(states_node);
	//itno
	rapidxml::xml_node<> *itno_node = doc.allocate_node(rapidxml::node_element, itno_node_str, doc.allocate_string("0"));
	states_node->append_node(itno_node);
	///None-xml file
	std::ofstream oFile;
	if (!outFile2.empty())
	{
		oFile.open(outFile2);
		if (oFile.is_open())
		{
			oFile << count << "\n";
		}
	}
	//xagent each
	for (unsigned int i = 0; i < count;i++)
	{
		rapidxml::xml_node<> *xagent_node = doc.allocate_node(rapidxml::node_element, xagent_node_str);
		{
			rapidxml::xml_node<> *name_node = doc.allocate_node(rapidxml::node_element, name_node_str, circle_str);
			xagent_node->append_node(name_node);

			sprintf(buffer, "%d", i);
			rapidxml::xml_node<> *id_node = doc.allocate_node(rapidxml::node_element, id_node_str, doc.allocate_string(buffer));
			xagent_node->append_node(id_node);
			
			fBuffer[0] = rng_float(rng);
			sprintf(buffer, "%.9g", fBuffer[0]);
			rapidxml::xml_node<> *x_node = doc.allocate_node(rapidxml::node_element, x_node_str, doc.allocate_string(buffer));
			xagent_node->append_node(x_node);

			fBuffer[1] = rng_float(rng);
			sprintf(buffer, "%.9g", fBuffer[1]);
			rapidxml::xml_node<> *y_node = doc.allocate_node(rapidxml::node_element, y_node_str, doc.allocate_string(buffer));
			xagent_node->append_node(y_node);

			fBuffer[2] = rng_float(rng);
			sprintf(buffer, "%.9g", fBuffer[2]);
			rapidxml::xml_node<> *z_node = doc.allocate_node(rapidxml::node_element, z_node_str, doc.allocate_string(buffer));
			xagent_node->append_node(z_node);

			rapidxml::xml_node<> *fx_node = doc.allocate_node(rapidxml::node_element, fx_node_str, zero_pt_zero_str);
			xagent_node->append_node(fx_node);

			rapidxml::xml_node<> *fy_node = doc.allocate_node(rapidxml::node_element, fy_node_str, zero_pt_zero_str);
			xagent_node->append_node(fy_node);

			rapidxml::xml_node<> *fz_node = doc.allocate_node(rapidxml::node_element, fz_node_str, zero_pt_zero_str);
			xagent_node->append_node(fz_node);

			///None xml output
			if (oFile.is_open())
			{
				sprintf(&buffer[0], "%.9g,%.9g,%.9g\n", fBuffer[0], fBuffer[1], fBuffer[2]);
				oFile << buffer;
			}
		}
		states_node->append_node(xagent_node);
	}
	//Close none xml stream
	if (oFile.is_open())
	{
		oFile.close();
	}
	//Actually do the xml output
	oFile.open(outFile);

	oFile << doc;

	oFile.close();
}
