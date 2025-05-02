# Estágio de construção (build)
FROM node:18 as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Estágio de produção
FROM nginx:alpine

# Remove a configuração padrão do Nginx
RUN rm -rf /etc/nginx/conf.d/default.conf

# Copia nossa configuração customizada do Nginx
COPY ./nginx/nginx.conf /etc/nginx/conf.d/default.conf

# Copia APENAS os arquivos compilados do estágio de construção
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]