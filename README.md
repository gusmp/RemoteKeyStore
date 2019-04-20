1.What is Remote KeyStore?
===========================

Have you ever been in an organization in which your digital certificates are scattered across your servers?  If you answer is yes, then Remote KeyStore might give you a helping hand.

From the back office you will be:

* Add / remove / suspend certificates
* See logs, thus who, when and where consume your certificates

2.Set up Remote KeyStore
===========================

2.1 Remote KeyStore back office
--------------------------------

Just follow the following steps:

1. Download the source of the subfolder *RemoteKeyStoreBO*
2. Specify the path of your embedded database in the file *RemoteKeyStoreBO\src\main\resources-local\dataBaseConfig.properties* property *dataSource.url*
3. Compile it with the command *mvn clean package*
4. Deploy the war in the *target* in a JSP/Servlet container like Apache Tomcat
5. Check if all is working accessing to http://server:port/RemoteKeyStoreBO
6. The default configuration set two admin users: *admin* and *admin2* with password *admin* and *admin2* respectively.
7. Load a certificate. Be aware that the pin of the certificate **must be the same** when you use in your client application.

The default setting are in the folder *RemoteKeyStoreBO\src\main\resources-local*. Feel free to copy these files an move to other profile. If you do these compile with:

mvn clean package -P *profile name*

2.2 Compile Remote RemoteKey
----------------------------

1. Download the project *RemoteKeyStore*
2. Modify the variable *REMOTE_KEYSTORE_BO_URL* with the url of your Remote KeyStore back office
3. Compile it with: *mvn clean install*

2.3 Change your application
---------------------------

1. Add the dependency:

```
<dependency>
  <groupId>org.gusmp.remotekeystore.remotekeystore</groupId>
  <artifactId>RemoteKeyStore</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

Instead of using *java.security.KeyStore* use *org.gusmp.remotekeystore.RemoteKeyStore*

Check the *DemoApp.java* from the *DemoApp* project to see how to use it. As it can be see, it is practically the same as KeyStore class.

2. Test it!

3.Further settings
====================

3.1 Communications between RemoteKeyStore and RemoteKeyStore back office
-------------------------------------------------------------------------

The communications between RemoteKeyStore and RemoteKeyStore back office are protected by user/password credentials and the messages are ciphered with AES.

In RemoteKeyStore these properties are in java code:

* user in private variable *APP_USER*
* password in private variable *APP_PASSWORD*
* key to cipher communications in private variable *COMMUNICATIONS_CIPHER_KEY*


From the RemoteKeyStore back office perspective, they can be found in the file *config.properties* inside the appropriate profile

3.2 Data base support
--------------------

By default it is used Apache Derby because is simpler. But there are other options:

* HsqlDb
* MySQL

Other database back-ends are possible as long as the JDBC driver was provided (such as Oracle)
