FROM jenkins/jenkins:lts-jdk21

USER root

# Install kubectl
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" \
    && chmod +x kubectl \
    && mv kubectl /usr/local/bin/kubectl

# Install Docker CLI (needed to build images)
RUN apt-get update && apt-get install -y docker.io

USER jenkins