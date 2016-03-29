This is one of the projects of RSSFeedsProject which enables clients to register for receiving latest feeds for various groups of urls at a preconfigured interval.
Clients register websocket endpoints and latest feeds are published to the endpoint

RSS-Feed-Aggregator
===================

An RSS Feed Aggregator that fetches latest feeds for URLs that the user is interested in.

1) It publishes the latest feeds to the client websocket endpoint.

2) Once a client registers a web socket endpoint, a job scheduling is kicked off which fetches the latest feeds for the given urls at regular intervals and updates the user feed endpoint.

3) The process listens to a queue to receive messages for fetching latest feeds.

4) Once feeds are fetched, it publishes the status to another publisher queue.

5) The publisher queue is constantly being monitored and once a message is received, the corresponding websocket endpoint is notified with the latest feeds.

Work in progress:

Publishing the latest feeds to the dynamic endpoints as per channel configured.

To run:

1) Run it as a simple java application.

2) The jar can be deployed to EC2 instances as well.


