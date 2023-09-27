
# tenure-cost-and-trade-records-frontend

This is service provides the frontend service for sending trade and cost data to the VOA for business rateable value.

This service is still under development but hoping to move to a limited private beta in November 2023. This is a limited private beta as we are not able to integrate with the new VOA systems until late 2024.

## Nomenclature

TCTR - Tenure Cost and Trade Records.

STaCI - Sent Trade and Cost Information.

FOR - form of return - forms used for users to send details of different types of business property.

## Prerequisite

* [SBT](https://www.scala-sbt.org/download.html)
* [Service Manager](https://github.com/hmrc/service-manager)
* [MongoDB](https://docs.mongodb.com/manual/installation/)

## Dependencies

* A local Mongo DB instance needs to run locally
* You can start the dependencies in service manger by running:
>sm2 --start VOA_TCTR   
>mongod
## Run the service
>sbt run

Then you can open in your browser the following url:
http://localhost:9526/send-trade-and-cost-information/logi

* Service manager

```
sm2 --start VOA_TCTR_FRONTEND
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").