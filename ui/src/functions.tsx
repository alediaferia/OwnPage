import React from 'react';
import NavigationBar from './NavigationBar';

interface INavigationBarProps {
  children: any,
  path: string
};

export function WithNavigationBar(props: INavigationBarProps) {
  return (
    <div>
      <NavigationBar />
      { props.children }
    </div>
  );
}