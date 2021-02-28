const proxy = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
    '/api',
    proxy({
      target: 'http://localhost:8456',
      changeOrigin: true,
      followRedirects: true,
      pathRewrite: {
          '^/api': ''
      }
    })
  );
};