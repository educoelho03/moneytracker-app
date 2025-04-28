# Stage 1: Build the React application
FROM node:lts-alpine

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

ARG APP_URL
ENV APP_URL=${APP_URL}

RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]
