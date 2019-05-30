# DroneDeliverySystem

This README file explains how to use the DroneDeliverySystem Application

---- BUILD AND RUN APPLICATION ON THE COMMAND LINE ----

•	Firstly, download and set up Maven (explained in APPENDIX).

•	Go to the DroneDeliverySystem app location in the command line

C:\{classpath*} \DroneDeliverySystem>
•	DroneDeliverySystem should have the maven's pom file. The pom file has all the dependencies and build configurations set up. Just run the following command

C:\{classpath*} \DroneDeliverySystem>mvn package
•	The above command builds the application successfully and creates target directory. In the target directory an executable jar file with name "DroneDeliveryService-0.0.1-SNAPSHOT.jar" is created.

•	To execute the jar file, give the following command along with command line variable input_file_path output_file_path

C:\{classpath*} \DroneDeliverySystem\target>java -jar DroneDeliveryService-0.0.1-SNAPSHOT.jar 10
•	The above command creates 10 customer threads to serve the customers


--- ASSUMPTIONS MADE ---

•	A Drone can carry a single package at a time

•	There is a single Drone operating for 1 input order list

•	Input file has the orders for the entire day

•	The orders in the input file are placed on the current day starting at 12 am

•	The total number hours required to serve the orders may be more than 16 hours which is 6am to 10pm

•	Even though we have the complete order list, a drone can't serve an order at any point in time if the order is placed at future time. For example we can't serve order placed at 10am at 8am.

•	Drone Scheduler, schedules for orders which are placed in past, i.e., at 8am, scheduler only schedules for orders placed before 8am.

•	Dispatching an order would take 2 times the travel duration of a the order destination, i.e., considering the to and fro travel time.


--- APPLICATION’S CLASSES SUMMARY ---

•	Main: The starting point of execution with main method. It checks the validity of input and output filepath and reads the input file to prepare a queue of current day orders. This calls the DroneScheduler to schedule and dispatch orders.

•	CustomerOrder: This is the class notation of the Order placed by a customer.

•	DroneScheduler: This is the actual business class of our Application, where all the orders get prioritized and delivered, as per the busniess rules that serve the Order which has more priority, priority is the sum of time in seconds of the duration of delivery and the time in seconds since the order is made.
    Greedy Approach - Consider 2 orders which have same delivery duration, then an order which is placed in distant past has less priority.

•	Heap: Used to sort the orders as per priority

•	Utility: Used for conversion to Time String to time in seconds and delivery direction to delivery duration.



--- APPENDIX ---

•	Download latest Maven version from this link, https://maven.apache.org/download.cgi.

•	Extract files and place it any classpath, copy the classpath and include it to system environment variables.
