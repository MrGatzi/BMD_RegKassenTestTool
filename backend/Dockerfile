FROM node:22-slim

RUN apt-get update && \
    apt-get install -y --no-install-recommends default-jre-headless && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci --omit=dev

COPY server.mjs ./

# Verification JARs
COPY ../regkassen-verification-depformat-1.1.1.jar ./jars/
COPY ../regkassen-verification-receipts-1.1.1.jar ./jars/

ENV JAR_DIR=/app/jars
ENV PORT=4000
EXPOSE 4000

CMD ["node", "server.mjs"]
