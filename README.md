RSS-Feed-Aggregator
===================

An RSS Feed Aggregator that pulls feeds from URLs that the user is interested in.

It publishes the latest feeds to the client websocket endpoint.

Once a client registers a web socket endpoint, a job scheduling is kicked off which fetches the latest feeds for the given urls at regular intervals and updates the user feed endpoint.

To run:
Run it as a simple java application.
The jar can be deployed to EC2 instances as well.
