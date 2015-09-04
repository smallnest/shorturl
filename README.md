# shorturl server
A scalable high performance ShortURL server
![](documents/demo.gif)

## Dependencies
It depends on：

* [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html):  ShortURL backend sever is implemented by Scala which is running on JVM.
* [Nginx](http://nginx.org/) (Optional):  Load balancer.
* [MongoDB](https://www.mongodb.org/):  nosql database for persisting mapping between short urls and real urls.  
* [Memcahced](http://memcached.org/) (Optional): cache server.

## Scalability
You can run this application but you should set up a cluster because ShortURL services serves for many requests as a product.
This project is designed for this requirement. You can scale your deployment easily, and even you can your existed cluster.

### Backend server
It is stateless so you can scale it as necessary.

### Load balancer
you can use Nginx or others as your load balancer. Remeber let your LB run as HA(High Availability)

### Database
set up a mongodb cluster as database and use urlId for sharding key.

### Cache  
set up multiple memcached servers.

More installation guide and design:  [Wiki](https://github.com/smallnest/shorturl/wiki)