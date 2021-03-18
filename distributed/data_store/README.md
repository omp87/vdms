When building this docker file behind a proxy, it is necessary to set the options for Maven through the command line. The following command shows and example:

docker build --build-arg  MAVEN_OPTS="-Dhttp.proxyHost=<<HTTP_PROXY_HOST_VALUE>> -Dhttp.proxyPort=<<HTTP_PROXY_PORT_VALUE>> -Dhttps.proxyHost=<<HTTPS_PROXY_HOST_VALUE>> -Dhttps.proxyPort=<<HTTPS_PROXY_PORT_VALUE>>" .
