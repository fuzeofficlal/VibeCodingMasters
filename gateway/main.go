package main

import (
	"log"
	"net/http"
	"net/http/httputil"
	"net/url"

	"github.com/gin-gonic/gin"
	"portfolio-gateway/middleware"
)

const (
	gatewayPort = ":8090"
	backendURL  = "http://localhost:8080"
)

func main() {
	r := gin.Default()

	// Apply global middlewares
	r.Use(middleware.CORS())
	r.Use(middleware.RateLimit())

	// Proxy static frontend files from Spring Boot
	target, _ := url.Parse(backendURL)
	rp := httputil.NewSingleHostReverseProxy(target)
	rp.ErrorHandler = func(w http.ResponseWriter, r *http.Request, err error) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusBadGateway)
		_, _ = w.Write([]byte(`{"status":502,"message":"Backend service unavailable. Please start the Spring Boot server."}`))
	}

// Proxy business APIs (v1 direct backend bypass support)
	r.Any("/api/v1/*proxyPath", func(c *gin.Context) {
		proxyPath := c.Param("proxyPath")
		c.Request.URL.Path = "/api/v1" + proxyPath
		rp.ServeHTTP(c.Writer, c.Request)
	})

    // Python Market Data Reverse Proxy
	marketTarget, _ := url.Parse("http://localhost:8000")
	marketRP := httputil.NewSingleHostReverseProxy(marketTarget)
	marketRP.ErrorHandler = func(w http.ResponseWriter, r *http.Request, err error) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusBadGateway)
		_, _ = w.Write([]byte(`{"status":502,"message":"Market Data service unavailable. Please start Python FastAPI."}`))
	}

	// Formal Gateway API (v2)
	r.Any("/api/v2/*proxyPath", func(c *gin.Context) {
		proxyPath := c.Param("proxyPath")
		
		// If path starts with /market or /debug routing to appropriate backend
		if len(proxyPath) > 7 && proxyPath[:7] == "/market" {
			// Rewrite to v1 for Python compatibility
			c.Request.URL.Path = "/api/v1" + proxyPath
			marketRP.ServeHTTP(c.Writer, c.Request)
		} else {
			// Proxy to Java Backend (rewriting v2 -> v1 backward compatibility)
			c.Request.URL.Path = "/api/v1" + proxyPath
			rp.ServeHTTP(c.Writer, c.Request)
		}
	})

	// Proxy Swagger documentation
	r.Any("/swagger-ui/*proxyPath", func(c *gin.Context) {
		proxyPath := c.Param("proxyPath")
		c.Request.URL.Path = "/swagger-ui" + proxyPath
		rp.ServeHTTP(c.Writer, c.Request)
	})

	r.Any("/v3/*proxyPath", func(c *gin.Context) {
		proxyPath := c.Param("proxyPath")
		c.Request.URL.Path = "/v3" + proxyPath
		rp.ServeHTTP(c.Writer, c.Request)
	})

	// Fallback route for all other requests (static content routing)
	r.NoRoute(func(c *gin.Context) {
		rp.ServeHTTP(c.Writer, c.Request)
	})

	log.Printf("🚀 Portfolio API Gateway started on http://localhost%s", gatewayPort)
	log.Printf("   Proxying v2 default to backend: %s", backendURL)
	log.Printf("   CORS: Enabled | Rate Limit: 20 req/s")

	if err := r.Run(gatewayPort); err != nil {
		log.Fatalf("Gateway failed to start: %v", err)
	}
}
