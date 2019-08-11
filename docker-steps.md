Run locally
===
    mvn clean compile package -Pdocker
    sudo docker build -t app .
    sudo docker run -p 8080:8080 app


Run on AWS EC2:
=== 

    Referene:  https://docs.aws.amazon.com/AmazonECR/latest/userguide/docker-basics.html

### 1. Install AWS CLI:
	$sudo apt install awscli

### 2. AWS configure:
    Follow https://docs.aws.amazon.com/IAM/latest/UserGuide/getting-started_create-admin-group.html for access key id and secret to create IAM user
	$aws configure    >> asks you to enter id and secret key created earlier ( can download csv )

### 3. Create repo
    $aws ecr create-repository --repository-name MY_REPOSITORY --region REGION

    The above returns Output: note repositorUri
    {
        "repository": {
            "repositoryArn": "arn:aws:ecr:us-east-1:159931644654:repository/noteapp",
            "registryId": "159931644654",
            "repositoryName": "noteapp",
            "repositoryUri": "159931644654.dkr.ecr.us-east-1.amazonaws.com/noteapp",
            "createdAt": 1565552020.0,
            "imageTagMutability": "MUTABLE"
        }
    }

### 4. Build image and tag
    $docker tag MY_DOCKER_APP_IMAGE repositoryUri   << repositoryUri from above output

### 5. Login to ECR docker
    $aws ecr get-login --no-include-email --region region  << returns a command, run it

### 6. Push to repo
    $docker push repositoryUri   << repositoryUri from above output

### 7. View ECR Repository to verify, note the region on url
    https://us-east-1.console.aws.amazon.com/ecr/repositories?region=us-east-1

Example
===
 
    $ aws configure
    $ sudo aws ecr create-repository --repository-name noteapp --region us-east-1    << returned repositoryUri": "159931644654.dkr.ecr.us-east-1.amazonaws.com/noteapp
    $ mvn clean compile package -Pdocker
    $ sudo docker build -t app .
    $ sudo docker tag app 159931644654.dkr.ecr.us-east-1.amazonaws.com/noteapp
    $ aws ecr get-login --no-include-email --region us-east-1   << otherwise we get error: ``denied: The security token included in the request is invalid.``
    $ sudo docker push 159931644654.dkr.ecr.us-east-1.amazonaws.com/noteapp

### Create and Login to EC2 instance 
    https://docs.aws.amazon.com/AmazonECS/latest/developerguide/get-set-up-for-amazon-ecs.html
   
   
Terms:

- EC2 - alternative of beanstalk -- allows to create a machine and clusters
- ECR - container registry -- we can push docker images
- IAM - identity mgmt --> need to create a second account before creating EC2 instances/ publish image on ECR etc .. give full access


- Need to create EC2 cluster to deploy docker image
- Create EC2 cluster, a service, task using the docker image and run the task to deploy the image
- A task should be created to do the deploy
