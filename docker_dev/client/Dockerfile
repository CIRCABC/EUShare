FROM node:12.22
WORKDIR /app

RUN npm install -g @angular/cli
COPY package.json ./package.json
RUN npm install
CMD ng serve --host 0.0.0.0

