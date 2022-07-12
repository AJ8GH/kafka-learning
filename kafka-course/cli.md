# Kafka CLI

## Topics

### create

`kafka-topics --zookeeper 127.0.0.1:2181 --topic <topic_name> --create --partitions <int> --replication-factor <int>`

### list

`kafka-topics --zookeeper 127.0.0.1:2181 --list`

### delete

`kafka-topics --zookeeper 127.0.0.1:2181 --topic <topic_name> --delete`  

### describe

`kafka-topics --zookeeper 127.0.0.1:2181 --topic <topic_name> --describe`

## Producers

### create a producer

`kafka-console-producer --broker-list 127.0.0.1:9092 --topic <topic_name>`

### create producer with property

`kafka-console-producer --broker-list 127.0.0.1:9092 --topic <topic_name> --producer-property acks=all`

(adds acknowledgement to all messages)

## Consumers

create a consumer to listen to all messages from now.

`kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic <topic_name>`

create a consumer to listen to all messages from the beginning of the topic

`kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic <topic_name> --from-beginning`

create a consumer group

`kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic <topic_name> --group <group_name>`

## Consumer groups

### list all consumer groups

`kafka-consumer-groups --bootstrap-server localhost:9092 --list`

### describe a group

`kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group <group_name>`

### reset offsets

`--reset-offsets`
