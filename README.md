# Kotlin Native network client
Network client written with native API 

### Install with Maven

To start work with library add a dependency to build.gradle

```
 val commonMain by getting {
            dependencies {
                implementation("io.github.anioutkazharkova:kn_network_client:1.0.0")
            }
        }
```        
Do not forget to specify mavenCentral as available repo source.
```
 repositories {
        //..
        mavenCentral()
    }
```    

### How to use

Just create HttpClient in common code (just like this):

```
import com.azharkova.kmm_network.HttpClient

class DI {
    @ThreadLocal
    companion object DI {
        val instance = DI()
    }

    val networkClient: HttpClient by lazy {
        HttpClient()
    }

    val newsService: NewsService by lazy {
        NewsService(networkClient)
    }
}
```

And use it:

```
class NewsService constructor(val networkClient: HttpClient) {

    suspend fun getNewsList(): ContentResponse<NewsList> {
        val response = networkClient.request(
            Request(url = NEWS_LIST, method = Method.GET, 
            headers = hashMapOf("X-Api-Key" to API_KEY,
        "Content-Type" to "application/json", 
        "Accept" to "application/json")))

       val news: ContentResponse<NewsList> = JsonDecoder.instance.decode(response.content.orEmpty())
        return news
    }
```

Also you can use built-in JsonDecoder to decode all your data in specific ContentResponse<T>.

Or use your own mapper and work with Response data class.

What is supported:

[x]Specify methods of http request
[x]Specify headers for request   
[x]Native url session for iOS
[x]OkHttp for Android
  
What will be supported (todo):
[ ]Body posting
[ ]Extended work with parameters
[ ]Extended specification of iOS client mechanism
[ ]Extended request logic  
