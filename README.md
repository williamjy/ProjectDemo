# General
# DO NOT COPY
## Installation of Repository
1. Obtain a copy of the repository on https://github.com/COMP3900-9900-Capstone-Project/capstoneproject-comp3900-w18a-nuggets in order to obtain the source code. You may choose a plethora of methods such as clone, download as zip or HTTPS.
    * If you do not have access to the repository and require access, please contact the administrator via z5264837@cse.unsw.edu.au.

## Running Application
1. Follow `Running Frontend Application`.
2. Follow `Running Backend Application`.

# Frontend
## Setting Up Frontend Environment
1. If the project is run on the CSE Linux environment, skip to step 3 as yarn should already be installed.
2. Yarn is required in order to run the application. Follow the guide https://classic.yarnpkg.com/en/docs/install/#windows-stable to find a suitable method of installation whether it is by npm or via an installer.
    * In most instances, developers already have npm installed so for convenience enter the command `npm install --global yarn`.
3. Node is also required in order to run the application. https://nodejs.org/en/ provides the installer package where you have to install and run the installer.

## Running Frontend Application
1. If you have not already set up the front end environment, follow `Setting Up Frontend Environment`.
2. Open a terminal where its current directory is the backend folder which should look like {project_root_location}/frontend.
3. Install the required dependencies using `yarn install --ignore-engines` in the terminal.
4. To start the frontend server, use `yarn start` in the terminal. This will try to take localhost:3000.
    * A quick way to set up the server is to use `yarn install --ignore-engines; yarn start` incase of new dependencies.
6. When you want to stop the application, you may kill it in any method you want. Some examples are listed below.
    * Pressing Ctrl + C while in focus on the terminal
    * kill {process_id}
    * kill -9 {process_id}

## Running Frontend Tests
1. If you have not already set up the front end environment, follow `Setting Up Frontend Environment`.
2. Open a terminal where its current directory is the frontend folder which should look like {project_root_location} frontend.
3. In the test suite, there are many styles of tests. Enter the desired command.
    * Unit Test: `yarn test` - Should take less than 10 seconds as of 2/8/2021.
    * Integration Test - Should take less than 2 minutes as of 2/8/2021.
        * Terminal: `yarn cypress run`
        * GUI: `yarn cypress open`


# Backend
## Running Backend Application
1. Open a terminal where its current directory is the backend folder which should look like {project_root_location}/backend.
2. To run the application, enter `./mvnw spring-boot:run` in the terminal. This will install the required dependencies and start the server. The server will commence on localhost:8080.
3. When you want to stop the application, you may kill it in any method you want. Some examples are listed below.
    * Pressing Ctrl + C while in focus on the terminal
    * kill {process_id}
    * kill -9 {process_id}

## Running Backend Tests
1. Open a terminal where its current directory is the backend folder which should look like {project_root_location}/backend.
2. Run the tests by typing `./mvnw test` in the terminal.
3. Wait for the tests to complete. This should take less than 30 seconds as of 2/8/2021.
