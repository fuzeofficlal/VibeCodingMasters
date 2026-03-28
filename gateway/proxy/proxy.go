package proxy

import (
	"fmt"
	"net/http"
	"net/http/httputil"
	"net/url"

	"github.com/gin-gonic/gin"
)

// NewReverseProxy 返回一个将请求转发到 target 的反向代理 Handler
func NewReverseProxy(target string) gin.HandlerFunc {
	targetURL, err := url.Parse(target)
	if err != nil {
		panic(fmt.Sprintf("invalid proxy target URL: %s", target))
	}

	rp := httputil.NewSingleHostReverseProxy(targetURL)

	// 自定义错误处理：当后端不可达时，返回友好错误
	rp.ErrorHandler = func(w http.ResponseWriter, r *http.Request, err error) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusBadGateway)
		_, _ = w.Write([]byte(`{"status":502,"message":"Backend service is unavailable. Please ensure Spring Boot is running on port 8080."}`))
	}

	return func(c *gin.Context) {
		// 修正转发路径：保留完整的 path
		c.Request.URL.Path = c.Param("proxyPath")
		rp.ServeHTTP(c.Writer, c.Request)
	}
}
