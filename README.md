# protohandler
A solution for linking Protobuf with Netty.

## Motivation
Default [Protobuf](https://github.com/protocolbuffers/protobuf) handlers included in [Netty](https://github.com/netty/netty) library entail creating 2 handlers (1 for encoding & 1 for decoding) by message.
This solution aims to provide a way for using only 2 handlers for all Protobuf messages. It comes in 2 flavors :
* The simple (and heavyweight) flavor add directly the message name into the encoded message which will be send.
* The compact (and lightweight) flavor add a message identifier into the encoded message which will be send. Caution, you will need to map identifiers with messages first. 

### What should I do with this?
Enhance it?
Use it as a starting point for your own solution?
