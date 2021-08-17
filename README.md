# Kinesis and Spring POC

Proof of Concept to create a Kinesis Data Stream Producer/Consumer using Spring Boot and AWS Kinesis Client Library.

This POC doesn't use any Spring-Cloud dependency.

## Configuration

### Create Kinesis Stream

- Open https://console.aws.amazon.com/kinesis/home?region=us-east-1#/streams/list
- Create Data Stream. I recommend create one with at least 3 Shards. So you can start until 3 simultaneous consumers.

### Running Service

- Define your AWS Credentials (AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY) as environment variables
```shell
export AWS_ACCESS_KEY_ID=<your_access_key>
export AWS_SECRET_ACCESS_KEY=<your_secret_access_key>
export AWS_REGION=<aws region>
```
- Define the Kinesis Data Stream name that you created as environment variable. You can define a KINESIS_APPLICATION_NAME to change default application name used (`kinesispoc`)
```shell
export KINESIS_STREAM_NAME=<my_stream_name>
```
- Execute application
```shell
gradle bootRun
```
- You can do requests to send User messages to Stream with the following command:
```shell
curl --location --request POST 'http://localhost:8080/user' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "John Doe",
    "gender": "M"
}'
```