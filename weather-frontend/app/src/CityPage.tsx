import * as React from 'react';
import './App.css';
import 'antd/dist/antd.css';
import { message, Table, Form, Input, Button } from 'antd';
import { ChangeEvent, FormEvent } from 'react';
import { instance } from './axios';
import * as qs from 'qs';

interface IAppState {
	datas: any[];
	cityName: string;
	cityCode: string;
}
class CityPage extends React.Component<any, IAppState> {
	public tableColumns: any[] = [
		{
			title: '编码',
			width: '25%',
			dataIndex: 'code',
			key: 'code'
		},
		{
			title: '名称',
			dataIndex: 'name',
			key: 'name'
		},
		{
			title: 'change',
			width: '25%',
			key: 'state',
			render: (data: any) => {
				return (
					<div>
						{!data.state ? (
							<Button
								onClick={() => {
									this.updateCrawlState(data.id, true);
								}}
							>
								启用
							</Button>
						) : (
							<Button
								onClick={() => {
									this.updateCrawlState(data.id, false);
								}}
							>
								禁止
							</Button>
						)}
						<Button
							onClick={() => {
								this.remove(data.id);
							}}
						>
							删除
						</Button>
					</div>
				);
			}
		}
	];

	constructor(props: any) {
		super(props);
		this.state = {
			datas: [],
			cityCode: '',
			cityName: ''
		};
	}

	public componentDidMount() {
		instance.get('city/list').then(data => {
			if (data.status === 200) {
				this.setState({
					datas: data.data
				});
			} else {
				message.error('城市数据加载失败');
			}
		});
	}

	public render() {
		return (
			<div className="App">
				<div style={{ margin: '20px 50px', textAlign: 'left' }}>
					<Button
						onClick={() => {
							instance.post('weather/crawl').then(data => {
								message.success('已提交抓取请求，请耐心等待...');
							});
						}}
					>
						抓取数据
					</Button>
					<Button
						style={{ marginLeft: '12px' }}
						onClick={() => {
							instance.post('weather/refresh').then(data => {
								message.success('已提保存数据取请求，请耐心等待...');
							});
						}}
					>
						保存临时数据
					</Button>
					<p style={{ marginTop: '5px' }}>
						系统每天晚上自动查询数据，此处主动执行抓取数据的功能，可以即使查看数据
					</p>
				</div>
				<header>
					<h1 className="App-title">查询城市添加</h1>
				</header>

				<Form layout="inline" onSubmit={this.handleSubmit}>
					<Form.Item>
						<Input
							style={{ width: 300 }}
							value={this.state.cityName}
							onChange={this.onChangeCityName}
							placeholder="请添加城市"
						/>
					</Form.Item>
					<Form.Item>
						<Input
							style={{ width: 300 }}
							value={this.state.cityCode}
							onChange={this.onChangeCityCode}
							placeholder="请添加城市编码"
						/>
					</Form.Item>
					<Form.Item>
						<Button type="primary" htmlType="submit">
							添加
						</Button>
					</Form.Item>
				</Form>
				<Table
					rowKey={data => data.id}
					pagination={false}
					columns={this.tableColumns}
					dataSource={this.state.datas}
				/>
			</div>
		);
	}
	private onChangeCityName = (e: ChangeEvent<HTMLInputElement>) => {
		this.setState({
			cityName: e.target.value
		});
	};
	private onChangeCityCode = (e: ChangeEvent<HTMLInputElement>) => {
		this.setState({
			cityCode: e.target.value
		});
	};
	private remove = (id: string) => {
		console.log(qs.stringify({ id }));
		instance.post('city/remove', { id }).then(
			data => {
				if (data.status === 200) {
					this.setState({
						datas: this.state.datas.filter(it => {
							return it.id !== id;
						})
					});
					message.success('删除城市成功');
				} else {
					message.error('删除城市失败');
				}
			},
			err => {
				message.error('删除城市失败:' + err);
			}
		);
	};
	private updateCrawlState = (id: string, state: boolean) => {
		instance.post('city/update', { id, state }).then(
			data => {
				if (data.status === 200 && data.data) {
					this.setState({
						datas: this.state.datas
							.map(it => {
								if (it.id === data.data.id) {
									return data.data;
								} else {
									return it;
								}
							})
							.slice()
					});
					message.success('更新城市成功');
				} else {
					message.error('更新城市失败');
				}
			},
			err => {
				message.error('更新城市失败:' + err);
			}
		);
	};
	private handleSubmit = (e: FormEvent) => {
		e.preventDefault();
		if (!this.state.cityCode || !this.state.cityName) {
			message.info('城市名称和城市编码不能为空');
			return;
		}
		if (!/^\d+$/.test(this.state.cityCode)) {
			message.info('城市编码必须为只包括数字的字符串');
			return;
		}
		instance
			.post('city/create', {
				name: this.state.cityName,
				code: this.state.cityCode
			})
			.then(
				data => {
					if (data.status === 200 && data.data) {
						this.setState({
							datas: [...this.state.datas, data.data],
							cityCode: '',
							cityName: ''
						});
						message.success('添加城市成功');
					} else {
						message.error('添加城市失败');
					}
				},
				err => {
					message.error('添加城市失败:' + err);
				}
			);
	};
}

export default CityPage;
