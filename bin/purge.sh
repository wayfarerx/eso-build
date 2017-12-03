#!/usr/bin/env bash
#
curl -X DELETE "https://api.cloudflare.com/client/v4/zones/e5b7e3a58848fdda2a17bacc7ce1bb62/purge_cache" -H "X-Auth-Email: x@wayfarerx.net" -H "X-Auth-Key: $1" -H "Content-Type: application/json" --data '{"purge_everything":true}'
