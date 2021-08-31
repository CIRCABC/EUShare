FROM node:12.22
WORKDIR /app
COPY dist/angular ./
RUN npm install -g @angular/cli
RUN npm install
CMD ng serve -c dev --host 0.0.0.0
