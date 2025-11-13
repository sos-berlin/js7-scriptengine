var process = { env: { NODE_DEBUG: false } };

class JS7Job extends js7.Job {

	processOrder(js7Step) {
		//js7Step.getLogger().info("[process]" + Object.getOwnPropertyNames(process));
		var url = require('url');
		var httpClient = require('http-client');
		//var axios = require('axios');

		const myURL = url.parse('https://www.google.com/foo');
		js7Step.getLogger().info("[url]" + Object.getOwnPropertyNames(myURL));
		js7Step.getLogger().info("[url.host]" + myURL.host);
		js7Step.getLogger().info("[url.href]" + myURL.href);


		js7Step.getLogger().info("[httpClient]" + Object.getOwnPropertyNames(httpClient));



		/** 
		const urlx = "https://httpbin.org/get";
		httpClient.fetch(urlx)
			.then(response => {
				for (const pair of response.headers) {
					console.log("${pair[0]}: ${pair[1]}");
				}
				return response.text();
			}).then(data => {
				console.log(data);
			});

		*/

		/**
				axios.get('https://jsonplaceholder.typicode.com/posts/1')
					// Show response data
					.then(res => js7Step.getLogger().info(res.data))
					.catch(err => js7Step.getLogger().error(err))*/
	}
}
