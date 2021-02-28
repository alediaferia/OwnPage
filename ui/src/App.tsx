import React from 'react';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link as RouterLink
} from "react-router-dom";

import LoginPage from "./LoginPage";
import './App.css';
import HomePage from './HomePage';
import AuthenticationPage, { AuthenticationType } from './AuthenticationPage';
import PostCreatePage from './PostCreatePage';
import PostViewPage from './PostViewPage';
import { WithNavigationBar } from './functions';

function App() {
  const homePath = "/home"
  const loginPath = "/login"
  const authenticatedPath = "/authenticated"
  const guestAuthenticatedPath = "/guest/authenticated"
  const postsNewPath = "/posts/new"
  const postsViewPath = "/posts/:postId"
  return (
      <Router>
        {/* <div className="App">
          <NavigationBar />
        </div> */}
        <Switch>
          <Route
            path={homePath}
            render={(props) => (<WithNavigationBar path={homePath} {...props} ><HomePage /></WithNavigationBar>)} />
          <Route
            path={loginPath}
            render={(props) => (<WithNavigationBar path={loginPath} {...props}><LoginPage /></WithNavigationBar>)} />
          <Route
            path={authenticatedPath}
            render={(props) => (<WithNavigationBar path={authenticatedPath} {...props}><AuthenticationPage authType={AuthenticationType.Own} uri="/authenticated" /></WithNavigationBar>)}
            />
          <Route
            path={guestAuthenticatedPath}
            render={(props) => (<WithNavigationBar path={guestAuthenticatedPath} {...props}><AuthenticationPage authType={AuthenticationType.Guest} uri="/guest/authenticated" /></WithNavigationBar>)}
            />
          <Route
            path={postsNewPath}
            render={(props) => (<WithNavigationBar path={postsNewPath} {...props}><PostCreatePage /></WithNavigationBar>)}
            />
          <Route
            path={postsViewPath}
            render={(props) => (<WithNavigationBar path={postsViewPath} {...props}><PostViewPage /></WithNavigationBar>)}
            />
        </Switch>
      </Router>
    );
}

export default App;
