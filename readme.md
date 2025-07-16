## README.md for Multi-Source Aggregated Search Platform

In order to build ElasticSearch, Kibana, Canal, and logstash to build data connection, do following steps:

1. Download zip file from 
ES: https://www.elastic.co/guide/en/elasticsearch/reference/7.17/setup.html
Kibana: https://www.elastic.co/guide/en/kibana/7.17/introduction.html
Install ik-analysis plugins...


(Edition 7.17.29 is used)

logstash: https://www.elastic.co/downloads/past-releases/logstash-7-17-29

, and follow instructions to engineer binlog and canal on https://github.com/alibaba/canal/wiki/QuickStart.

2. Unzip downloaded files

3. Add PATH to ~/.zshrc or ~/.bashrc, according to computer architecture, and source

4. Go to /bin file and use command to start
# I use "./bin/elasticsearch" for es and "kibana" for kibana before start




When starting frontend, use "npm install --force" to install yarn.
Go to package.json and you can see three commands: "serve", "build", "lint". 

  "scripts": {
    "serve": "vue-cli-service serve",
    "build": "vue-cli-service build",
    "lint": "vue-cli-service lint"
  },

You can use "npm run serve" to start frontend development environment. 

baseURL can be modified in myAxios.ts



When starting backend, connect database to local MySQL. 
Go to create_table.sql to execute, creating table. 
Waiting for maven dependency installing. 
Choose Java Edition >= 8(suggest to be 8)
Go to MainApplication.java to run or debug main function.

