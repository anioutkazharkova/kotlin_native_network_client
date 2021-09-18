import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject var model = TestModel()

    var body: some View {
        Text(verbatim: model.text).onAppear(perform: {
            model.loadRequest()
        })
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
    ContentView()
    }
}

class TestModel : ObservableObject {
    var client = HttpClientCommon()
    @Published var text: String = ""
    
    func loadRequest() {
        
        
         let request = Request(url: "https://newsapi.org/v2/top-headlines?language=en", method: Method.get, headers: ["X-Api-Key": "5b86b7593caa4f009fea285cc74129e2", "Content-Type": "application/json", "Accept":"application/json"])
       // client.request(request: request) { response, error in
         //   if let content = response?.content {
           //     print("content: \(content)")
            //}
        //}
      client.request(request: request){ (response) in
            if let content = response.content {
                print("content: \(content)")
            }
        }
        //client.req
    }
}

