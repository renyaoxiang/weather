import axios from 'axios';

export const instance = axios.create({
	baseURL: process.env.NODE_ENV === 'development' ? 'http' + '://127.0.0.1:9900' : '',
	timeout: 1000,
	headers: {
		'Access-Control-Allow-Origin': '*',
		// 'Content-Type': 'application/x-www-form-urlencoded'
		'content-type':'application/json'
	}
});
