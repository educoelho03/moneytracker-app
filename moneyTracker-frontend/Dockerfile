FROM --platform=linux/x86_64 node:18-alpine as build-stage

WORKDIR /app/frontend

COPY package*.json ./

RUN npm install

COPY . .

ARG APP_URL
ENV APP_URL=${APP_URL}

RUN npm run build

FROM --platform=linux/x86_64 nginx:latest as production-stage

COPY --from=build-stage /app/frontend/dist /usr/share/nginx/html
COPY --from=build-stage /app/frontend/nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

RUN ln -snf /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && echo "America/Sao_Paulo" > /etc/timezone

CMD ["nginx", "-g", "daemon off;"]
