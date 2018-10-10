import { Button, Form, DatePicker, Table, message, AutoComplete } from 'antd';
import axios from 'axios';
import * as React from 'react';
import './App.css';
import 'antd/dist/antd.css';
import { SelectValue } from 'antd/lib/select';
import { Moment } from 'moment';
const FormItem = Form.Item;
const instance = axios.create({
	baseURL: process.env.NODE_ENV === 'development' ? 'http' + '://127.0.0.1:8080' : '',
	timeout: 1000
});
interface IAppState {
	cities: string[];
	city?: string;
	day?: Moment;
	weatherDataList: any[];
}
class App extends React.Component<any, IAppState> {
	public tableColumns: any[] = [
		{
			title: '时间',
			dataIndex: 'data1',
			key: 'data1'
		},
		{
			title: '温度',
			dataIndex: 'data3',
			key: 'data3'
		},
		{
			title: '风向',
			dataIndex: 'data4',
			key: 'data4'
		},
		{
			title: '风力',
			dataIndex: 'data5',
			key: 'data5'
		},
		{
			title: '降水',
			dataIndex: 'data6',
			key: 'data6'
		},
		{
			title: '相对湿度',
			dataIndex: 'data7',
			key: 'data7'
		},
		{
			title: '空气质量',
			dataIndex: 'data8',
			key: 'data8'
		}
	];

	constructor(props: any) {
		super(props);
		this.state = {
			cities: [],
			city: undefined,
			day: undefined,
			weatherDataList: []
		};
	}

	public componentDidMount() {
		instance.get('city/info').then(data => {
			if (data.status === 200) {
				this.setState({
					cities: Object.keys(data.data)
				});
			} else {
				message.error("城市数据加载失败")
			}
		});
	}

	public render() {
		return (
			<div className="App">
				<header>
					<h1 className="App-title">天气数据查询</h1>
				</header>
				<Form layout="inline" onSubmit={this.handleSubmit}>
					<FormItem>
						<AutoComplete
							dataSource={this.state.cities}
							allowClear={true}
							value={this.state.city || ''}
							showSearch={true}
							style={{ width: 300 }}
							placeholder="选择城市"
							onChange={this.onChangeCity}
							filterOption={(input: any, option: any) => {
								return option.props.children.indexOf(input) >= 0;
							}}
						/>
					</FormItem>
					<FormItem>
						<DatePicker
							placeholder="选择日期"
							style={{ width: 300 }}
							value={this.state.day || undefined}
							onChange={this.onDateChange}
						/>
					</FormItem>
					<FormItem>
						<Button type="primary" htmlType="submit">
							查询
						</Button>
					</FormItem>
				</Form>
				<Table columns={this.tableColumns} dataSource={this.state.weatherDataList} />
			</div>
		);
	}
	private handleSubmit = (e: React.FormEvent<any>) => {
		e.preventDefault();
		if (!this.state.city) {
			message.error('请选择城市');
			return;
		}
		if (!this.state.day) {
			message.error('请选择日期');
			return;
		}
		const day = this.state.day.format('YYYY-MM-DD');
		instance.get(`weather/city/${this.state.city}/${day}`).then(data => {
			if (data.status === 200) {
				this.setState({
					weatherDataList: data.data
				});
			} else {
				message.error(data.statusText);
			}
		});
	};
	private onDateChange = (value: any) => {
		this.setState({ day: value });
	};
	private onChangeCity = (value: SelectValue) => {
		this.setState({ city: value as string });
	};
}

export default App;
