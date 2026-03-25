# Use a public mirror of Docker Official Images to avoid Docker Hub pull limits in CI.
ARG NODE_BASE_IMAGE=public.ecr.aws/docker/library/node:20-alpine

# ---- Build Stage ----
FROM ${NODE_BASE_IMAGE} AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN mkdir -p /app/public
ENV NEXT_TELEMETRY_DISABLED=1
RUN npm run build

# ---- Runtime Stage ----
FROM ${NODE_BASE_IMAGE} AS runner

WORKDIR /app
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

RUN addgroup --system --gid 1001 nodejs && \
    adduser --system --uid 1001 nextjs

COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs
EXPOSE 3000
ENV PORT=3000

CMD ["node", "server.js"]

