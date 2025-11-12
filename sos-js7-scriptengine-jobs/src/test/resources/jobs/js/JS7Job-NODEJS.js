var process = { env: { NODE_DEBUG: false } };

class JS7Job extends js7.Job {

	processOrder(step) {
		//step.getLogger().info("[process]" + Object.getOwnPropertyNames(process));
		var url = require('url');
		var httpClient = require('http-client');
		//var axios = require('axios');

		const myURL = url.parse('https://www.google.com/foo');
		step.getLogger().info("[url]" + Object.getOwnPropertyNames(myURL));
		step.getLogger().info("[url.host]" + myURL.host);
		step.getLogger().info("[url.href]" + myURL.href);


		step.getLogger().info("[httpClient]" + Object.getOwnPropertyNames(httpClient));



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
					.then(res => step.getLogger().info(res.data))
					.catch(err => step.getLogger().error(err))*/
	}
}
