# Netty IMS

Distributed netty based IMS cluster.

to create custer on docker:

```shell
docker-compose up
```

Techs used

- Netty: network library
- Nginx: loadbalancer and websocket proxy
- Zookeeper: data storage for user and server information
- Kafka: message broker between the servers
