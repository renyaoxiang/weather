import { Button, Form, Table, message, AutoComplete } from 'antd';
import * as React from 'react';

import './App.css';
import 'antd/dist/antd.css';
import { SelectValue } from 'antd/lib/select';
import * as moment from 'moment';
import { instance } from './axios';
const FormItem = Form.Item;

interface IAppState {
	temp: boolean;
	cities: string[];
	city?: string;
	dayinfoDataList: any[];
}
class DayinfoPage extends React.Component<any, IAppState> {
	public tableColumns: any[] = [
		{
			title: '时间',
			dataIndex: 'day',
			width: '10%',
			render: (data: any) => {
				return moment(data).format('YYYY-MM-DD');
			}
		},
		{
			title: '日出',
			dataIndex: 'sunrise',
			width: '10%',
			key: 'data2'
		},
		{
			title: '日落',
			dataIndex: 'sunset',
			width: '10%',
			key: 'data4'
		}
	];

	constructor(props: any) {
		super(props);
		const path: string = props.match.path || '';
		this.state = {
			temp: path.includes('temp'),
			cities: [],
			city: undefined,
			dayinfoDataList: []
		};
	}

	public componentDidMount() {
		instance.get('city/list').then(data => {
			if (data.status === 200) {
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
					<h1 className="App-title">日出日落数据查询</h1>
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
						<Button type="primary" htmlType="submit">
							查询
						</Button>
					</FormItem>
				</Form>
				<Table rowKey={data => data.id} columns={this.tableColumns} dataSource={this.state.dayinfoDataList} />
			</div>
		);
	}
	private handleSubmit = (e: React.FormEvent<any>) => {
		e.preventDefault();
		if (!this.state.city) {
			message.info('请选择城市');
			return;
		}
		instance.get(`dayinfo/city/${this.state.city}`, { params: { temp: this.state.temp } }).then(
			data => {
				if (data.status === 200) {
					this.setState({
						dayinfoDataList: data.data
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
	private onChangeCity = (value: SelectValue) => {
		this.setState({ city: value as string });
	};
}

export default DayinfoPage;
