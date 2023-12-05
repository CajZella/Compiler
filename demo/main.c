int getnum(int num) {
	return num * num + 1;
}

int main() {
	int num;
	num = 100;
	if(num <= 0) {
		int i = 0;
		while(i < 11) {
			if(i == 10) {
				break;
			}
			if(i == 1) {
				continue;
			}
			i = i + 1;
		}
	}
	else {
		int i = 0;
		i = getnum(num);
	}
	return 0;
}