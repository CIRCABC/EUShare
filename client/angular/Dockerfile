FROM node:alpine as builder
COPY package.json package-lock.json ./
RUN npm ci && mkdir /angular && mv ./node_modules ./angular
WORKDIR /angular
COPY . .
RUN npm run  builddocker
FROM nginx:alpine
COPY nginx/default.conf /etc/nginx/conf.d/
RUN rm -rf /usr/share/nginx/html/*
COPY --from=builder /angular/dist /usr/share/nginx/html
CMD ["nginx", "-g", "daemon off;"]
