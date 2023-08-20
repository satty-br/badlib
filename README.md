# badlib
## Introduction

Welcome to the "badlib" project documentation. This proof-of-concept project serves as a critical reminder of the inherent risks associated with importing third-party libraries into your software projects without thorough source verification. Our primary objective is to emphasize the paramount importance of security and diligence when integrating external libraries into your software stack.

## Getting Started

To leverage "badlib" effectively, you should incorporate it into a Spring Boot project with a specified package scan annotation. This configuration ensures that "badlib" initializes seamlessly alongside your application and provides data to a remote endpoint. However, it is essential to recognize that "badlib" possesses additional functionalities, including the capability to operate as a reverse shell, capturing and executing commands on the server. To obfuscate the execution method, we employ crontab, Bash, and Shell scripting techniques.

## Configuration 

1. Begin by executing the "server/server.py" file. This step is crucial as it sets up the foundational infrastructure.
2. Next, scrutinize the package being imported. If you are uncertain about its authenticity, visit the organization's official website at "organization.com.br." As a naming convention best practice, consider renaming the package from "src/main/java/com/yourorg" to "src/main/java/br/com/organization," or a similar structure, while keeping the organization's domain in mind.
3. Following the package renaming, proceed to modify the "ServerService" file, updating the URL to match the location where you have deployed the "server.py" script.

## Installation
1. Deploy package in your artifactory 
2. Import lib in your project

Conclusion

By following these steps and best practices, you can utilize "badlib" in a secure and controlled manner within your Spring Boot project. Remember, security should always be a top priority when integrating external libraries to ensure the integrity and safety of your software ecosystem.


