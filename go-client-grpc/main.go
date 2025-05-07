package main

import (
	"context"
	"log"
	"time"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"

	pb "gRPC-Practice/grpc-go-client/pingpong"
)

func main() {
	conn, err := grpc.Dial(
		"localhost:8080",
		grpc.WithTransportCredentials(insecure.NewCredentials()),
	)
	if err != nil {
		log.Fatalf("Failed to connect: %v", err)
	}
	defer conn.Close()

	client := pb.NewPingPongServiceClient(conn)

	for i := 1; i <= 10; i++ {
		req := &pb.PingRequest{
			Message:   "Ping from Go (Request #" + string(rune(i)) + ")",
			Timestamp: time.Now().UnixMilli(),
			Id:        int64(i * 100),
		}

		res, err := client.Ping(context.Background(), req)
		if err != nil {
			log.Fatalf("RPC failed: %v", err)
		}

		log.Printf("[Request #%d] Response: %s", i, res.Message)
		log.Printf("[Request #%d] Timestamp: %d", i, res.Timestamp)
		log.Printf("[Request #%d] ID: %d", i, res.Id)

		if i < 10 {
			time.Sleep(5 * time.Second)
		}
	}
}
