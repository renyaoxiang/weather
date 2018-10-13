import { Button, Form, DatePicker, Table, message, AutoComplete } from 'antd';
import * as React from 'react';
import './App.css';
import 'antd/dist/antd.css';
import { SelectValue } from 'antd/lib/select';
import { Moment } from 'moment';
import { instance } from './axios';
const FormItem = Form.Item;

interface IAppState {
	cities: string[];
	city?: string;
	day?: Moment;
	weatherDataList: any[];
}
class WeatherPage extends React.Component<any, IAppState> {
	public tableColumns: any[] = [
		{
			title: '时间',
			dataIndex: 'data1',
			width: '10%',
			key: 'data1'
		},
		{
			title: '温度',
			dataIndex: 'data2',
			width: '10%',
			key: 'data2'
		},
		{
			title: '风向',
			dataIndex: 'data4',
			width: '10%',
			key: 'data4'
		},
		{
			title: '风力',
			dataIndex: 'data5',
			width: '10%',
			key: 'data5'
		},
		{
			title: '降水',
			dataIndex: 'data6',
			width: '10%',
			key: 'data6'
		},
		{
			title: '相对湿度',
			dataIndex: 'data7',
			width: '10%',
			key: 'data7'
		},
		{
			title: '空气质量',
			dataIndex: 'data8',
			width: '10%',
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
		instance.get('city/list').then(data => {
			if (data.status === 200) {
				console.log(data.data);
				this.setState({
					cities: data.data
						.map((it: any) => {
							return { value: it.code, text: it.name };
						})
						.filter((it: any) => !it.code)
				});
			} else {
				message.error('城市数据加载失败');
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
				<Table
					pagination={false}
					rowKey={data => data.id}
					columns={this.tableColumns}
					dataSource={this.state.weatherDataList}
				/>
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
		instance.get(`weather/city/${this.state.city}/${day}`).then(
			data => {
				if (data.status === 200) {
					this.setState({
						weatherDataList: data.data
					});
					message.success('操作成功');
				} else {
					message.error(data.statusText);
				}
			},
			err => {
				message.error(err);
			}
		);
	};
	private onDateChange = (value: any) => {
		this.setState({ day: value });
	};
	private onChangeCity = (value: SelectValue) => {
		this.setState({ city: value as string });
	};
}

export default WeatherPage;
