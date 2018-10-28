import * as React from 'react';
import './App.css';
import 'antd/dist/antd.css';

import { HashRouter as Router, Route, Link, Redirect } from 'react-router-dom';
import WeatherPage from './WeatherPage';
import DayinfoPage from './DayinfoPage';

import CityPage from './CityPage';
import { Menu } from 'antd';

class App extends React.Component<any, any> {
	constructor(props: any) {
		super(props);
	}

	public render() {
		return (
			<div>
				<Router>
					<div>
						<Route
							path="/:path"
							render={router => {
								return (
									<Menu
										defaultSelectedKeys={[router.location.pathname.replace('/', '')]}
										mode="horizontal"
									>
										<Menu.Item key="city">
											<Link to="/city">city</Link>
										</Menu.Item>
										<Menu.Item key="dayinfo">
											<Link to="/dayinfo">dayinfo</Link>
										</Menu.Item>
										<Menu.Item key="weather">
											<Link to="/weather">weather</Link>
										</Menu.Item>
									</Menu>
								);
							}}
						/>

						<Route path="/" exact={true} render={() => <Redirect to="/weather" />} />
						<Route path="/weather" component={WeatherPage} />
						<Route path="/dayinfo" component={DayinfoPage} />
						<Route path="/city" component={CityPage} />
					</div>
				</Router>
			</div>
		);
	}
}

export default App;
