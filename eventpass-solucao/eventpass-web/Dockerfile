FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 5173
# O "-- --host" é obrigatório para o Vite aceitar conexões de fora do container local
CMD ["npm", "run", "dev", "--", "--host"]