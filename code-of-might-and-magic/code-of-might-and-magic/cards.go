package main

type creature struct {
	instanceID int
	cost       int
	attack     int
	defense    int
}

func filter(cards []creature, test func(creature) bool) (ret []creature) {
	for _, s := range cards {
		if test(s) {
			ret = append(ret, s)
		}
	}
	return
}
