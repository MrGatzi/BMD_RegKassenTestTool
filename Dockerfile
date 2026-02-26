FROM node:22-slim

RUN apt-get update && \
    apt-get install -y --no-install-recommends default-jre-headless && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Install web dependencies and build
COPY web/package.json web/package-lock.json ./web/
RUN cd web && npm ci --omit=dev

COPY web/ ./web/
RUN cd web && npm run build

# Copy verification JARs to where the app expects them (parent of web/)
COPY regkassen-verification-depformat-1.1.1.jar .
COPY regkassen-verification-receipts-1.1.1.jar .
COPY regkassen-demo-1.0.0.jar .

ENV NODE_ENV=production
ENV PORT=3000
EXPOSE 3000

WORKDIR /app/web
CMD ["npm", "start"]
