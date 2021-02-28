FROM node:10.16.3 as build

RUN groupadd -r ownuser && useradd --no-log-init -r -g ownuser -m ownuser
USER ownuser
WORKDIR /home/ownuser
COPY --chown=ownuser:ownuser . .
RUN yarn install --production --pure-lockfile && yarn build

FROM nginx:stable
RUN mkdir -p /usr/local/www/ownpage && \
  chown nginx:nginx /usr/local/www/ownpage && \
  rm -rf /etc/nginx/conf.d
COPY --chown=nginx:nginx --from=build /home/ownuser/build /usr/local/www/ownpage
COPY --chown=nginx:nginx --from=build /home/ownuser/nginx/nginx.conf /etc/nginx/nginx.conf
COPY --chown=nginx:nginx --from=build /home/ownuser/nginx/conf.d /etc/nginx/conf.d
USER nginx
