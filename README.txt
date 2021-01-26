Distributed Key Value Store with Confiurable Consistency : Using Apache Thrift RPC Framework
- Harshit Vadodaria (harshitv95@gmail.com)

Similar to Facebook's Cassanda Database, this is a custom implementation of a Distributed Key
value store following the same principles as Cassandra like
	- Configurable Consistency,
	- Hinted Handoffs,
	- Memtables,
	- Write-Ahead Logging (for failure recovery),
and is a always-availability Data Store, with great Performance.

Features a very generic Cassandra-like Database project structure (interfaces and abstract classes)
that supports RPC. Any RPC framework would work with this project, including (but not limited to)
Apache Thrift, ggRPC, Avro, Java RMI, Protobuf, REST and SOAP APIs etc.

This project includes a simple implementation using Apache Thrift RPC Framework, but it can be
extended to work with any other RPC Framework.

Steps to build and run:
The executable 'server' can be used to build and execute a KeyValue Store Server.
If you do not have permissions to execute 'server' on your system, simply execute (one-time setup):
chmod +100 server

Once you have the permissions, execute:
./server port

where port=any available port number you want to let this server listen on
example:
./server 9090

This should compile the code, build an executable jar, and execute it with the port parameter

Run the client as follows:
./client coord=ip:port

Provide the IP:port of the node that you want the client to connect to, and that node will
act as a coordinator for the entire session