# Kafka Theory

## Topics, Partitions and Offsets

Topics: a particular stream of data
* similar to a table in a database
* you can have as many topics as you want
* a topic is identified by its **name**

Topics are split in partitions
* Each partition is ordered (starting from 0)
* Each message within a partition gets an incremental id, called offset, these are infinite
* Need to specify how many partitions you want when you create a topic
* Each partition is independent and can have different number of messages
* Data is kept for limited time - default one week
* Data is Immutable - once written to a partition it can't be changed
* If a key is not provided, data is assigned randomly to a partition

## Brokers

Brokers hold topics. Each broker is a server

Each broker is identified by an id (always a number)

Each broker contains certain topic partitions

## Clusters

A cluster is composed of multiple brokers. (multiple machines / servers)

When you connect to one kafka broker, you are connected to the entire cluster

Good number to get started on is 3 brokers, but big clusters can have over 100 brokers

## Brokers and topics

Topic-A has 3 partitions
Topic-B has 2 partitions

Broker 101       | Broker 102       | Broker 103
-----------------|------------------|------------
TA - Partition 0 | TA - Partition 2 | TA- Partition 1
TB - Partition 1 | TB - Partition 0 |

data is distributed.
e.g. Broker 103 has no Topic-B data

## Topic replication factor 

When creating topic, you need to decide on replication factor.  This way when a broker is down, another one can serve the data. 3 is gold standard. 2 can be risky.

replication factor refers to how many copies of the data you have. with replication factor of 2, there are 2  copies of the data.

At any time, only ONE broker can be a leader for a given partition. Only that leader can receive and serve data for a partition.

The other brokers will synchronize the data

Each partition has one leader and multiple ISR (determined by Zoo Keeper)

## Producers

Producers write data to topics (made of partitions)

producers automatically know which broker and partition to write to

In case of broker failure, producers will automatically recover.

If data is sent without a key, the data will be sent round-robin between the partitions. Automatically load balanced

Producers can choose to receive acknowledgment (confirmation) of data writes

3 acknowledgement modes:
* acks=0 - dangerous, no waiting for acknowledgement (possible data loss)
* acks=1 - default, will wait for leader acknowledgement (limited data loss)
* acks=all - safest, leader + replicas all wait for acknowledgement (no data loss)

## Message keys

Producers can choose to send a key with the message

key can be anything you want (string, number etc.)

If key=null, data is sent round robin across partitions and brokers

If a key is sent, all messages for that key will always go to the same partition 

A key is sent if you need message ordering for a specific field

e.g. gps data messages from trucks with truck id, longitude, latitude:

key is truck_id - a specific truck id will always go to the same partition. Uses hashing. You don't choose the partition it goes to, but you know it will always go to the same one.

## Consumers

Consumers read data from a topic (identified by name)

Consumers know which broker to read from automatically

In case of broker failures, consumers already know how to recover

Data is read in order within each partition. e.g. offset 3 will always be read AFTER offset 2 in the same partition

Consumers can read from multiple partitions or just one, or none.

There is no guaranteed order across partitions - they are read in parallel, e.g. will read a little bit from one partition, then a bit from another and so on.

Consumer is essentially a java application

## Consumer groups

You can have a lot of consumers which read data in groups.

each consumer within a group will read data from exclusive partitions.

If you have more consumers than partitions, some will be inactive

usually you don't want this, but you can have extra ones as backup in case one goes down.

consumer group represents an application.

consumers will know automatically which partitions to read from, using a GroupCoordinator and a ConsumerCoordinator to assign a consumer to a partition.

## Consumer offsets

Kafka has the capability to store the offsets at which a consumer group has been reading. Like checkpointing or bookmarking.

The offsets are committed live in a kafka topic named `__Consumer_offsets`

When a consumer in a group has processed the data received from kafka, it should be committing the offsets (writing to the consumer offsets) - this is done automatically

If a consumer dies, it will be able to read back from where it left off, thanks to the committed consumer offsets.

## Delivery Semantics for consumers

Consumers choose when to commit offsets

there are 3 delivery semantics:
* At most once (usually not preferred:
    * offsets are committed as soon as the message is received
    * If the processing goes wrong the message will be lost (it won't be read again)
* At least once (usually preferred):
    * offsets are committed only after the message has been processed
    * if the processing goes wrong the message will be read again
    * this can result in duplicate processing of messages. Make sure your processing is idempotent (processing messages twice won't impact your systems) 
* Exactly once (the holy grail):
    * can only be achieved in Kafka to Kafka workflows, using kafka streams API 
    * for Kafka to External System workflows, use an idempotent consumer.
    
## Kafka broker discovery

Every kafka broker is a bootstrap server

Means you only need to connect to one broker and you will be connected to the entire cluster

Each broker knows about all the brokers, topics and partitions (Metadata)

e.g. a Kafka client (producer or consumer) connects to a broker within a Kafka Cluster. Once connection is established the client automatically makes a metadata request. The broker returns the metadata, including list of all the brokers and their IPs etc. 

This is all automatic but important to be aware of

## Zookeeper

Zookeeper managers brokers (keeps a list of them)

Helps in performing leader elections for partitions

Sends notifications to Kafka in case of changes (e.g. new topic, broker dies, broker comes up, delete topics, etc.)

**Kafa can't work without Zookeeper**

Zookeeper by design operates with an odd number of servers (3, 5, 7, etc.)

Zookeeper has a leader (handles writes) and the rest of the servers are followers (handle reads)

Zookeeper does not store consumer offsets since Kafka >v0.10

## Kafka Guarantees

Messages are appended to a topic-partition in the order they are sent

Consumers read messages in the order they are stored in a topic partition

With a replication factor of `N`, producers and consumers can tolerate up to `N - 1` brokers being down

This is why replication factor of 3 is a good idea:
* Allows for one broker to be take down for maintenance
* Allows for another broker to go down unexpectedly
* And we still have a working broker operating

As long as the number of partitions remains constant for a topic (no new partitions), the same key will always go to the same partition

## Theory round up

### Source Systems
sends data to Producers

### Producers
sends data to brokers

### Brokers
servers within a kafka cluster

### Consumers
consume data from kafka

### Target systems
receives data from consumers

### Zookeeper
manages kafka cluster and maintains list of all brokers
