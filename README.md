# MARC2MAVIS
## MARC to MAVIS Collection Record Transformer

### User Guide

#### Java CLI tool
Built for the American Folklife Center at the Library of Congress, custom, by AVPreserve  
2017-04-27

#### Requirements
* Java Runtime Environment (JRE) or Java Development Kit (JDK)
* MARC to MAVIS Collection Record Transformer dist folder

#### Instructions
1. Unzip and copy the [MARC to MAVIS Collection Record Transformer zip file](https://github.com/avpreserve/MARC2MAVIS/blob/master/MARC-to-MAVIS-Collection-Record-Transformer.zip) onto your desktop (or preferred location of your choice)
2. Open CMD (Command Prompt) on your Windows machine
3. Navigate to the dist folder within the MARC to MAVIS Collection Record Transformer folder
4. Initiate the validator with this command: java -jar Mavis3.jar
5. The program will ask for the path to the MARC XML file that you wish to parse (please name the file with no “spaces” in the filename)
6. Provide the path and press enter
7. The program will ask for a path for a folder where you want the results to end up (the tool generates a parsed XML file with multiple MAVIS Collections for each MARC record in the MARC XML file it processes)
8. Provide the path and press enter
9. Review the results and take your final XML file to MAVIS for import
10. If errors occur, the tool will report in the Command Prompt that something went wrong and no XML will be generated for that particular file.
