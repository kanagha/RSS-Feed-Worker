This is one of the projects of RSSFeedsProject which enables clients to register for receiving latest feeds for various groups of urls at a preconfigured interval.
Clients register websocket endpoints and latest feeds are published to the endpoint

RSS-Feed-Aggregator
===================

An RSS Feed Aggregator that fetches latest feeds for URLs that the user is interested in.

It publishes the latest feeds to the client websocket endpoint.

Once a client registers a web socket endpoint, a job scheduling is kicked off which fetches the latest feeds for the given urls at regular intervals and updates the user feed endpoint.

The process listens to a queue to receive messages for fetching latest feeds.
Once feeds are fetched, it publishes the status to another publisher queue.
The publisher queue is constantly being monitored and once a message is received, the corresponding websocket endpoint is notified with the latest feeds.

To run:
Run it as a simple java application.
The jar can be deployed to EC2 instances as well.
