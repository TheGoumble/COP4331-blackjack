# Dockerfile for Google Cloud Run deployment
FROM node:18-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy application files
COPY . .

# Expose port
EXPOSE 8080

# Cloud Run expects PORT environment variable
ENV PORT=8080

# Start the server
CMD ["node", "server.js"]
