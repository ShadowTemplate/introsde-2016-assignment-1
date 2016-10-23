# Assignment 1

**Introduction to Service Design and Engineering | University of Trento**

This file aims to provide a short documentation for the first course assignment.  
The original instructions can be found [here](https://sites.google.com/a/unitn.it/introsde_2016-17/lab-sessions/assignments/assignment-1).

## Project structure

The project repository is made up of the following *files* and **folders**:
* **src/health**: Java source code
    * **generated**: classes automatically generated by xjc from *people.xsd* with the command:
    
         ```
         $ xjc -d src -p health.generated people.xsd
         ```
        * *ObjectFactory.java*: factory to construct new instances of the Java representation for XML content
        * *People.java*: POJO for the root element in *people.xsd*
    * *Evaluator.java*: class containing the main method to be run with the Ant task
    * *Operator.java*: utility class 
* *README.md*: this file
* *build.xml*: Ant configuration file containing task definitions (see next paragraph)
* *ivy.xml*: Ivy configuration file containing project dependencies 
* *people.xml*: example file with people  
* *people.xsd*: schema definition for *people.xml*

According to the request, the *main* method in the *Evaluator* class completes the tasks by using the following methods:

1. *printPeople*: prints all the people in *people.xml* by evaluating the XPath expression

    ```
    //person
    ```
2. *printHealthProfile*: prints the health profile information for the person with a specific id in *people.xml* by evaluating the XPath expression

    ```
    /people/person[@id=input_id]/healthprofile
    ```
3. *searchByWeight*: finds people in *people.xml* satisfying a user-defined condition on their weight by evaluating the XPath expression   

    ```
    //healthprofile[weight input_operator input_value]/parent::person
    ```
4. *runXMLMarshalling*: generates random people and performs marshalling via JAXB storing data in a new XML file
5. *runXMLUnmarshalling*: performs unmarshalling via JAXB of the file created during the previous step
6. *runJSONMarshalling*: generates random people and performs marshalling via Jackson storing data in a new JSON file


## Project tasks

Some Ant tasks are defined inside *build.xml*. An overview of what each task does follows. Tasks' dependencies are in brackets:
* *download-ivy*: downloads Ivy jar from the Maven repository
* *install-ivy* (*download-ivy*): adds Ivy jar to the working directory
* *resolve* (*install-ivy*): downloads all the dependencies specified in the *ivy.xml* configuration file into the working directory
* *clean*: deletes the compilation folder from the workspace
* *init* (*install-ivy*, *resolve*, *clean*): initializes the workspace 
* *compile* (*init*): compiles the code 
* *execute.evaluation*: runs the *main* method contained into the *Evaluator* class and described above


## How to run

To run the code simply clone this project into your computer and run the *execute.evaluation* Ant task:
```
$ git clone https://github.com/ShadowTemplate/introsde-2016-assignment-1.git
$ cd introsde-2016-assignment-1
$ ant execute.evaluation
```
