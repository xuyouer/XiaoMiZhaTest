local key = KEYS[1]
local timeout = ARGV[1]

if redis.call('EXISTS', key) == 1 then
    return 1
end

redis.call('SET', key, '1', 'EX', timeout)
return 0
