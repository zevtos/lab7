To run the program:
```
git cloone https://github.com/zevtos/lab6


// located in lab6
mvn install

// located in server/client 
// Depending on which part of the application to run
mvn exec:java
```
For tunneling to helios(to run the server part on helios, and the client part on a PC and redirect requests to helios):
```
ssh -p 2222 -L XXXX:localhost:YYYY s'SSSSSS'@helios.cs.ifmo.ru
```
XXXX - the port that accepts requests from the local computer
YYYY - The port to which requests are redirected (on the server we are connecting to)
'SSSSSS' - student identification number to connect helios
