run:
	docker run --name demo-redis -v conf:/usr/local/etc/redis -p 6379:6379 -d redis

stop:
	docker stop demo-redis

rm: stop
	docker rm demo-redis
