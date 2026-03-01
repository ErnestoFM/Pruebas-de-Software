# Software Testing Project with Spring Boot and Maven

## Introduction
This README provides an overview of a software testing project developed using Spring Boot and Maven. The goal of this project is to demonstrate effective testing practices using popular tools in the Java ecosystem.

## Project Structure
The typical directory structure for this project includes:
```
src/
 ├── main/
 │   └── java/
 │       └── com/
 │           └── example/
 │               └── project/
 └── test/
     └── java/
         └── com/
             └── example/
                 └── project/
```

## Getting Started
To get started with the project, follow these instructions:

1. Clone the repository:
   ```bash
   git clone https://github.com/ErnestoFM/Pruebas-de-Software.git
   cd Pruebas-de-Software
   ```
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```

## Testing
To run the tests included in this project, use the following command:
```bash
mvn test
```

## Dependencies
This project uses the following dependencies for testing:
- Spring Boot Starter Test
- JUnit
- Mockito

## Conclusion
This project serves as an example of how to effectively implement testing in a Spring Boot application using Maven. Following the structure and commands outlined in this README, you should be able to set up your testing environment successfully.